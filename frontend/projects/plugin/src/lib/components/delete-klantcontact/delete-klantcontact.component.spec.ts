import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {DeleteKlantcontactComponent} from './delete-klantcontact.component';
import {DeleteKlantcontactConfig} from '../../models/delete-klantcontact-config';

describe('DeleteKlantcontactComponent', () => {
    let component: DeleteKlantcontactComponent;
    let fixture: ComponentFixture<DeleteKlantcontactComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        uuid: '',
    } satisfies Partial<DeleteKlantcontactConfig>;
    const validConfig = {...emptyConfig, uuid: 'x'} satisfies Partial<DeleteKlantcontactConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [DeleteKlantcontactComponent]
        })
            .overrideTemplate(DeleteKlantcontactComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(DeleteKlantcontactComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as DeleteKlantcontactConfig);

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
