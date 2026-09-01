import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {CreateKlantcontactComponent} from './create-klantcontact.component';
import {CreateKlantcontactConfig} from '../../models/create-klantcontact-config';

describe('CreateKlantcontactComponent', () => {
    let component: CreateKlantcontactComponent;
    let fixture: ComponentFixture<CreateKlantcontactComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        kanaal: '',
        onderwerp: '',
        taal: '',
        rol: '',
        objectId: '',
        codeObjecttype: '',
        codeRegister: '',
        codeSoortObjectId: '',
        resultPvName: '',
    } satisfies Partial<CreateKlantcontactConfig>;
    const validConfig = {...emptyConfig, kanaal: 'x', onderwerp: 'x', taal: 'x', rol: 'x', objectId: 'x', codeObjecttype: 'x', codeRegister: 'x', codeSoortObjectId: 'x', resultPvName: 'x'} satisfies Partial<CreateKlantcontactConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CreateKlantcontactComponent]
        })
            .overrideTemplate(CreateKlantcontactComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(CreateKlantcontactComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as CreateKlantcontactConfig);

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
