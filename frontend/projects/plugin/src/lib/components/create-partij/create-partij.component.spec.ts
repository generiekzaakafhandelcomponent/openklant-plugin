import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {CreatePartijComponent} from './create-partij.component';
import {CreatePartijConfig} from '../../models/create-partij-config';

describe('CreatePartijComponent', () => {
    let component: CreatePartijComponent;
    let fixture: ComponentFixture<CreatePartijComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        soortPartij: '',
        objectId: '',
        codeObjecttype: '',
        codeRegister: '',
        codeSoortObjectId: '',
        resultPvName: '',
    } satisfies Partial<CreatePartijConfig>;
    const validConfig = {...emptyConfig, soortPartij: 'x', objectId: 'x', codeObjecttype: 'x', codeRegister: 'x', codeSoortObjectId: 'x', resultPvName: 'x'} satisfies Partial<CreatePartijConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CreatePartijComponent]
        })
            .overrideTemplate(CreatePartijComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(CreatePartijComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as CreatePartijConfig);

        fixture.detectChanges();
    });

    afterEach(() => save$.complete());

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should report an incomplete configuration as invalid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(emptyConfig);

        expect(emitted).toEqual([false]);
    });

    it('should report a configuration with every required field as valid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(validConfig);

        expect(emitted).toEqual([true]);
    });

    it('should emit the configuration on save when it is valid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(validConfig);
        save$.next();

        expect(configurations).toEqual([validConfig]);
    });

    it('should not emit the configuration on save when it is invalid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(emptyConfig);
        save$.next();

        expect(configurations).toEqual([]);
    });
});
