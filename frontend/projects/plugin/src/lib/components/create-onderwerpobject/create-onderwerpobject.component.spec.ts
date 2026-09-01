import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {CreateOnderwerpobjectComponent} from './create-onderwerpobject.component';
import {CreateOnderwerpobjectConfig} from '../../models/create-onderwerpobject-config';

describe('CreateOnderwerpobjectComponent', () => {
    let component: CreateOnderwerpobjectComponent;
    let fixture: ComponentFixture<CreateOnderwerpobjectComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        resultPvName: '',
    } satisfies Partial<CreateOnderwerpobjectConfig>;
    const validConfig = {...emptyConfig, resultPvName: 'x'} satisfies Partial<CreateOnderwerpobjectConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CreateOnderwerpobjectComponent]
        })
            .overrideTemplate(CreateOnderwerpobjectComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(CreateOnderwerpobjectComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as CreateOnderwerpobjectConfig);

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
